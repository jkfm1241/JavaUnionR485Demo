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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jkfm1241.com.r485_demo.util.comsUtil.*;
import static org.jkfm1241.com.r485_demo.util.comsUtil.bytesToHexString;

@Slf4j
@Service
public class RequestQueryInfoServiceImpl implements RequestQueryInfoService {

    // 使用AtomicBoolean保证线程安全
    private static final AtomicBoolean portInUse = new AtomicBoolean(false);
    private static SerialPort currentPort = null;

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
    public synchronized boolean sendQuery() {
        // 检查端口是否已被占用
        if (portInUse.get()) {
            log.warn("串口 {} 已被占用，请求被拒绝", port);
            return false;
        }

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
            // 尝试获取端口使用权
            if (portInUse.compareAndSet(false, true)) {
                if (comPort.openPort()) {
                    currentPort = comPort;
                    log.info("端口 {} 已成功连接", port);

                    // 创建Modbus RTU消息
                    byte[] message = createModbusMessage(modbusProperties.getCommand());
                    log.info("发送:{} ",bytesToHexString(message));

                    // 发送数据
                    comPort.writeBytes(message, message.length);

                    // 等待并读取响应
                    int available = waitForResponse(comPort);

                    if (available > 0) {
                        byte[] responseBuffer = new byte[available];
                        int numRead = comPort.readBytes(responseBuffer, available);
                        if (numRead > 0) {
                            log.info("接收: {}",bytesToHexString(Arrays.copyOf(responseBuffer, numRead)));
                            //printHex(Arrays.copyOf(responseBuffer, numRead));
                            log.info("接收字节数：{}", responseBuffer.length);

                            // CRC校验
                            if (checkCRC(responseBuffer)) {
                                log.info("CRC校验通过!");

                                // todo 存入DB，将cmos接口响应结果，并记录时间

                                return true;
                            }
                        }
                    } else {
                        log.warn("系统未收到响应！");
                    }
                }
            }
            return false;
        } catch (SerialPortInvalidPortException e) {
            log.error("无效的串口: {}", port, e);
            return false;
        } finally {
            // 释放端口资源
            releasePort(comPort);
        }
    }

    private int waitForResponse(SerialPort comPort) {
        int available = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            available = comPort.bytesAvailable();
            if (available > 0) {
                break;
            }

            if (System.currentTimeMillis() - startTime > readtimeout) {
                available = -1;
                break;
            }

            try {
                Thread.sleep(100); // 添加短暂休眠减少CPU占用
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return available;
    }

    private synchronized void releasePort(SerialPort comPort) {
        if (comPort != null && comPort.isOpen()) {
            try {
                comPort.closePort();
                log.info("端口 {} 已关闭", port);
            } catch (Exception e) {
                log.error("关闭端口时发生异常", e);
            }
        }
        currentPort = null;
        portInUse.set(false);
    }
}