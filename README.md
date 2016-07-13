noti-J
=====

###소개
 * noti-J는 다수의 Application에서 사용자PC로 Message를 push해주는 소프트웨어 입니다.
 * noti-J는 Sender(전송), Gateway(중계), Receiver(수신)로 구성되어 있습니다.
 * Sender는 Java Application Liberary(jar)로 설치되며 JRE1.6이상에서 동작합니다.
 * Gateway는 javascript로 개발되어 있으며, node.js v0.10.26 기준으로 개발되었습니다.
 * Receiver는 Eclipse(4.3.1) RCP로 개발되어 있으며, Multi Platform(OS)을 지원합니다.

###라이선스
 * 이 소프트웨어는 집,회사등에서 무료로 자유롭게 사용할 수 있는 자유소프트웨어이다.
 * 이 소프트웨어는 오픈소스(GPL3.0) 라이선스를 준수한다.

###설치방법
 * Gateway
   * node.js를 설치한다. ( http://www.nodejs.org/ 참고)
   * notij-gateway-*.zip 파일을 다운로드 후 압축을 해제한다.
   * 실행명령(node gateway.js)을 통해 gateway를 실행한다.
 * Receiver(MS Window 기준)
   * notij-receiver*.zip 파일을 다운로드 후 압축을 해제한다.
   * eclipse/notij.ini 파일의 정보를 알맞게 수정한다.(사용법 참고)
   * eclipse/notij.exe 파일을 실행한다.
 * Sender
   * notij-sender*.zip 파일을 다운로드 후 압축을 해제한다.
   * lib 폴더 하위모든 jar파일을 Application classpath로 포함시킨다.

###사용법
 * Gateway
  * server.conf: Gateway의 환경설정파일로 JSON문법으로 구성되어있다.
   * host: Gateway설치 IP
   * rport: Receiver가 접속할 port번호
   * sport: Sender가 접속할 port번호
   * receivers: Receiver 인정정보
    * protocol: file, http (Receiver 인증정보를 file 또는 http를 통해 load한다.)
    * url: Receiver 인증정보 경로
    * reload: Receiver 인증정보 reload 주기(분)으로 0일경우 reload 하지 않는다.
   * senders: Sender Application 정보
    * ip: Sender Application IP Address(등록되어 있지 않을경우 접속을 허용하지 않는다.)
    * name: Sender Application Name
   * receiver.conf: Receiver 인증정보로 server.conf설정에 따라 파일명은 상이할수 있다.
    * id: 식별계정
    * name: 표시이름
    * passwd: 인증용 비밀번호
   * 실행방법: node gateway.js
   * 종료방법: Window(Ctrl+C), Linux(kill -9 pid)
   * Logging: Console Log는 info로 설정되어 있으며, debug Log는 logs폴더 하위 console.log파일에 저장된다.
   * 메세지저장: Recevier와 연결되지 않을경우 Gateway가 메세지를 보관하고 있다 종료시점시 temp폴더 하위 message.json 파일에 보관하고 Gateway재시작 시점에 load한다.

 * Receiver(MS Window 기준)
  * notij.ini: Receiver 환경설정 파일     
   * -Dnotij.host: Gateway IP Address
   * -Dnotij.port: Gateway Receiver Port
   * -Dnotij.userid: Gateway 인증 식별자
   * -Dnotij.passwd: Gateway 인증 비밀번호
   
 * Sender
  * SyncSender: Gateway로 즉시전송하며, Gateway와 연결되어 있지 않을경우 예외를 발생시킨다.
    ```
    NotiJSender sender = SyncSender.getInstance();
    sender.connect(host, port);
    sender.send(id, msg, url); 
    ```
  * ASyncSender: Gateway 연결되어 있지 않은경우도 예외를 발생시키지 않고
     메세지를 Queue에 저장후 연결이 복구되면 메세지를 전송한다.(단 Queue size를 초과하면 예외를 발생시킨다.)
    ```
    NotiJSender sender = AsyncSender.getInstance(queueSize);
    sender.connect(host, port);
    sender.send(id, msg, url);  
    ```

###사용예제
* Gateway 실행

  ![alt tag](https://raw.githubusercontent.com/jongbok/notij/master/docs/images/gateway.png)

* Sender Console 실행

  ![alt tag](https://raw.githubusercontent.com/jongbok/notij/master/docs/images/sender.png)

* Receiver 메세지 수신

  ![alt tag](https://raw.githubusercontent.com/jongbok/notij/master/docs/images/receiver.png)
 