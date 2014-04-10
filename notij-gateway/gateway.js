var winston = require('winston');
var net = require('net');
var fs = require('fs');
var request = require('request');
var async = require('async');
var config;
var receivers = [];
var messages = [];

var logger = new (winston.Logger)({
	transports : [ new (winston.transports.Console)({
		level : 'info',
		handleExceptions: true,
		timestamp: true
	}), new (winston.transports.File)({
		filename : 'logs/console.log',
		level : 'debug',
		handleExceptions: true,
		maxsize: 1024 * 1024 * 1
	}) ]
});

var sendSvr = net.createServer(function(sock) {
	sock.setEncoding('utf8');
	var valid = false;
	
	for (var i = 0; i < config.senders.length; i++) {
		if (config.senders[i].ip === sock.remoteAddress) {
			logger.info('Sender Connected: ' + sock.remoteAddress + ':' + sock.remotePort);
			valid = true;
			break;
		}
	}
	if(!valid){
		logger.error('Sender IP is not found: ' + sock.remoteAddress + ':' + sock.remotePort);
		sock.destroy();
		return;
	}

	sock.on('data', function(data){
		var input = JSON.parse(data);
		logger.debug('sender:: ' + data );
		switch(input.cmd){
			case 'send':
				var found = false;
				for(var i=0; i<receivers.length; i++){
					if(receivers[i].id === input.userid){
						if(receivers[i].sock !== null){
							receivers[i].sock.write(data);
							logger.debug('sender:' + sock.remoteAddress + ', receiver id:' + input.userid);
						}else{
							messages.push(input);
							logger.debug('message is saved: ' + data);
						}
						found = true;
						break;
					}
				}
				if(found){
					sock.write('success\n');
				}else{
					sock.write('error\n');
				}
				break;
			case 'live':
				var obj = {cmd:'live', state:'ok'};
				sock.write(JSON.stringify(obj) + '\n');
				break;
			case 'exit':
				logger.info('exit command received: ' + input.id + '\n');
				sock.destroy();
				break;
			default:
				logger.error('invalid command error: ' + input.cmd + '\n');
				sock.destroy();
		}
	});
	
	sock.on('error', function(err){
		sock.destroy();
		logger.error('Sender Error:' + err);
	});
});

var rcvSvr = net.createServer(function(sock) {
	sock.setEncoding('utf8');

	sock.on('data', function(data) {
		var input = JSON.parse(data);
		var obj;
		switch (input.cmd) {
		case 'login':
			logger.info('login command received: ' + input.id + '\n');
			for (var i = 0; i < receivers.length; i++) {
				if (receivers[i].login(input.id, input.passwd)) {
					if(receivers[i].sock){
						obj = {cmd:'login', message:'receiver is already connected!', login:'error'};
						sock.write(JSON.stringify(obj) + '\n');
						sock.destroy();
						logger.error(obj.message + ', id:' + input.id);
						return;
					}
					receivers[i].sock = sock;
					receivers[i].remoteAddress = sock.remoteAddress;
					receivers[i].remotePort = sock.remotePort;
					receivers[i].connectDate = new Date();
					sock.userid = receivers[i].id;
					obj = {cmd:'login', name:receivers[i].name, login:'success'};
					sock.write(JSON.stringify(obj) + '\n');
					logger.info(sock.remoteAddress + ':' + sock.remotePort + ' ' + input.id + ' was login!');
					for(var j=0; j<messages.length; j++){
						if(messages[j].userid === input.id){
							var msg = JSON.stringify(messages[j]);
							sock.write(msg + '\n');
							logger.debug('message send: ' + msg);
							messages.splice(j--, 1);
						}
					}
					
					logger.info('Receiver Connected: ' + sock.remoteAddress + ':' + sock.remotePort + ':' + input.id);
					return;
				}
			}
			obj = {cmd:'login', message:'user is not exists!', login:'error'};
			sock.write(JSON.springfy(obj) + '\n');
			sock.destroy();
			logger.error(obj.message + ' id:' + input.id);
			break;
		case 'live':
			obj = {cmd:'live', state:'ok'};
			sock.write(JSON.stringify(obj) + '\n');
			break;
		case 'exit':
			logger.info('exit command received: userid[' + input.id + ']\n');
			sock.destroy();
			break;
		default:
			logger.error('invalid command error: cmd[' + input.cmd + ']\n');
			sock.destroy();
		}
	});

	sock.on('close', function(){
		for(var i=0; i<receivers.length; i++){
			if(receivers[i].id === sock.userid){
				sock.userid = null;
				receivers[i].sock = null;
				logger.info('Receiver Disconnected: ' + receivers[i].remoteAddress + ':' + receivers[i].remotePort + '\n');
				break;
			}
		}
	});
	
	sock.on('error', function(err){
		sock.destroy();
		logger.error('Receiver Error:' + err);
	});
	
});

function ReceiverLogin(id, passwd){
	if(this.id === id && this.passwd === passwd){
		return this.name;
	}else{
		return false;
	}
}

function reloadReceiver(cb){
	var initial = function(){
		for(var i=0; i<receivers.length; i++){
			receivers[i].remoteAddress = '';
			receivers[i].remotePort = 0;
			receivers[i].sock = null;
			receivers[i].connectDate = null;
			receivers[i].login = ReceiverLogin;
		}
		logger.info('receivers are loaded!');
		if(typeof(cb) !== 'undefined'){
			cb(null);
		}
	};
	
	receivers.length = 0;
	if(config.receivers.protocol === 'file'){
		fs.readFile(config.receivers.url, 'utf8', function(err, data){
			receivers = JSON.parse(data);
			initial();
		});
	}else if(config.receivers.protocol === 'http'){
		request.get(config.receivers.url, function(error, response, body){
			if (!error && response.statusCode === 200) {
				receivers = JSON.parse(body);
				initial();
			}else{
				errMsg = 'cannot load receivers';
				logger.error(errMsg);
				throw new Error(errMsg);
			}
		});
	}else{
		var errMsg = 'receiver protocol is not valid!';
		logger.error(errMsg);
		throw new Error(errMsg);
	}
}

async.waterfall([
	function(cb){
		fs.readFile('conf/server.conf', 'utf8', function(err, data){
			logger.info('noti-J Server is starting...');
			config = JSON.parse(data);
			cb(null);
		});
	}
	, reloadReceiver
	, function(cb){
		if(config.receivers.reload > 0){
			var delay = config.receivers.reload * 1000 * 60;
			setInterval(reloadReceiver, delay);
		}
		cb(null);
	}
	, function(cb){
		if(fs.existsSync('temp/message.json')){
			var data = fs.readFileSync('temp/message.json', 'utf8');
			messages = JSON.parse(data);
		}
		cb(null);
	}
	, function(cb){
		rcvSvr.listen(config.rport, config.host);
		logger.info('Receiver Created at ' + config.host + ':' + config.rport);
		sendSvr.listen(config.sport, config.host);
		logger.info('Sender Created at ' + config.host + ':' + config.sport);
		cb(null);
	}]);

process.on('uncaughtException', function(err) {
	fs.writeFile('temp/message.json', JSON.stringify(messages), function(err){
		if(err){
			logger.error('cannot write temp messages! ' + err);
		}
	});
	logger.error('Caught exception: ' + err);
});

process.on('SIGINT', function(){
	fs.writeFile('temp/message.json', JSON.stringify(messages), function(err){
		if(err){
			logger.error('cannot write temp messages! ' + err);
		}else{
			logger.info('noti-J Server is shutdown!');
			process.exit(0);
		}
	});
});