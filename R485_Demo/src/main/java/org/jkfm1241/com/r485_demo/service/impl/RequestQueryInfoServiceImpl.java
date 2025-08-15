package org.jkfm1241.com.r485_demo.service.impl;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import lombok.extern.slf4j.Slf4j;
import org.jkfm1241.com.r485_demo.model.ModbusProperties;
import org.jkfm1241.com.r485_demo.service.RequestQueryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.jkfm1241.com.r485_demo.util.comsUtil.*;

@Slf4j
@Service
public class RequestQueryInfoServiceImpl implements RequestQueryInfoService {

    @Value("${custom.bps}")
    private Integer bps;

    @Value("${custom.datanum}")
    private Integer datanum;

    @Value("${custom.stopnum}")
    private Integer stopnum;

    @Value("${custom.check}")
    private Integer check;

    @Value("${cusotm.port}")
    private String port;

    @Value("${custom.readtimeout}")
    private Integer readtimeout;

    @Value("${custom.writetimeout}")
    private Integer writetimeout;

    @Autowired
    private ModbusProperties modbusProperties;

    @Override
    public void sendQuery() {

        SerialPort comPort = SerialPort.getCommPort(port);
        comPort.setBaudRate(bps);
        comPort.setNumDataBits(datanum);
        comPort.setNumStopBits(stopnum);
        comPort.setParity(check);

        // 设置读取超时
        comPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING, // 阻塞读取模式
                readtimeout,
                writetimeout
        );

        try {
            if( comPort.openPort() ){
                System.out.println("端口已连接！");

                // 创建Modbus RTU消息
                byte[] message = createModbusMessage( modbusProperties.getCommand() );
                // 显示发送的16进制数据
                System.out.println("发送: ");
                printHex(message);
                System.out.println();
                // 发送数据
                comPort.writeBytes(message, message.length);

                // 等待并读取响应
                int available = 0;
                // 开始时间
                long startTime = System.currentTimeMillis();
                while (true) {

                    // 返回当前可读字节数（不阻塞）。
                    available = comPort.bytesAvailable();
                    if( available > 0 ){
                        break;
                    }

                    // 超时控制
                    if (System.currentTimeMillis() - startTime > readtimeout) {
                        available = -1;
                        break;
                    }

                }

                if (available > 0) {
                    byte[] responseBuffer = new byte[available];
                    int numRead = comPort.readBytes(responseBuffer, available);
                    if (numRead > 0) {
                        System.out.print("接收: ");
                        printHex(Arrays.copyOf(responseBuffer, numRead)); // 打印实际收到的字节

                        System.out.println("接收字节数："+responseBuffer.length);

                        // CRC校验
                        if (checkCRC(responseBuffer)) {
                            System.out.print("CRC校验通过!");
                        }

                    }
                }else{
                    System.out.println("系统,未收到响应！");
                }


            }
        } catch (SerialPortInvalidPortException e) {
            // 程序异常
        } finally {
            // 关闭端口
            try {
                comPort.closePort();
            } catch (Exception e) {
                // 端口关闭异常
            }
            System.out.println("端口已关闭！");
        }

    }
}
