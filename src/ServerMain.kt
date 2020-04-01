import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.*
import java.util.*

const val ROOT = "C:\\PictureData"
fun main(args: Array<String>) {
    var path: String?
    var readDate: String
    var oldDate: String

    var date: Long
    var currentYear: Int
    var currentMonth: Int
    val receivePort = 1256
    val connectPort = 1255

    val serverLayout = ServerLayout()

    var socket: Socket?
    var serverSocket: ServerSocket

    var dis: DataInputStream
    var dos: DataOutputStream

    var fis: FileInputStream

    var filePath: File
    var scanner: Scanner?

    loop@ while (true) {

        // 1. 초기화
        // 1-1 폴더 생성
        path = ROOT
        filePath = File(path)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        serverLayout.mFilePath.text = filePath.absolutePath + "\\"

        // 1-2 파일이 있다면
        path += "\\LASTDATE"
        filePath = File(path)
        if (!filePath.exists()) {
            filePath.createNewFile()
            readDate = "0"
            serverLayout.mLastDate.text = "없음"
        } else {
            // 최신 날짜
            fis = FileInputStream(filePath)
            scanner = Scanner(fis)
            if (scanner.hasNext()) {
                readDate = scanner.next()
            } else {
                readDate = "0"
            }
            date = java.lang.Long.parseLong(readDate)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            currentYear = calendar.get(Calendar.YEAR)
            currentMonth = calendar.get(Calendar.MONTH) + 1
            serverLayout.mLastDate.text =
                currentYear.toString() + "-" + currentMonth.toString() + "-" + calendar.get(Calendar.DATE)
        }

        oldDate = readDate

        // 2. 클라이언트 기다리기
        // 리스너 소켓 생성 후 대기
        serverSocket = ServerSocket(receivePort)
        println("서버가 시작되었습니다.")
        serverLayout.mConnectInfo.text = "연결 대기중...."
        socket = serverSocket.accept()
        println("클라이언트와 연결 완료.")
        serverLayout.mProgressBar.value = 0
        serverLayout.mRemainFiles.text = ""
        serverLayout.mFileName.text = ""

        dis = DataInputStream(socket.getInputStream())
        var clientIp: String? = null
        try {
            clientIp = dis.readUTF()
        } catch (e: SocketException) {
            println("Ping timeout")
            serverSocket.close()
            socket.close()
            dis.close()
            continue
        }
        println("ClientIp $clientIp receive!")



        socket.close()
        dis.close()
        serverSocket.close()
        println("socket client ip : "+clientIp)

        socket = Socket(clientIp, connectPort)

        serverLayout.mConnectInfo.text = "클라이언트 연결 완료!~!"
        dos = DataOutputStream(socket.getOutputStream())
        val serverIp = InetAddress.getLocalHost().hostAddress
        dos.writeUTF(serverIp)
        println("Server IP: $serverIp 전송이 끝났습니다")

        dos.writeUTF(readDate)
        socket.close()
        println("동기화 날짜 전송 완료")

        dis?.close()
        dos?.close()
        serverSocket?.close()
        socket?.close()

    }

}