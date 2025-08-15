package org.jkfm1241.com.r485_demo.util;

import com.fazecast.jSerialComm.SerialPort;

import java.util.List;

public class comsUtil {

    // 创建Modbus消息并添加CRC
    public static byte[] createModbusMessage(int... data) {
        byte[] frame = new byte[data.length + 2]; // 数据+2字节CRC
        for (int i = 0; i < data.length; i++) {
            frame[i] = (byte) data[i];
        }

        // 计算CRC
        int crc = calculateCRC(frame, data.length);
        frame[data.length] = (byte) (crc & 0xFF);
        frame[data.length + 1] = (byte) ((crc >> 8) & 0xFF);

        return frame;
    }

    // 创建Modbus消息并添加CRC - 入参格式List集合
    public static byte[] createModbusMessage(List<Integer> data) {
        byte[] frame = new byte[data.size() + 2]; // 数据+2字节CRC
        for (int i = 0; i < data.size(); i++) {
            frame[i] = data.get(i).byteValue();
        }
        // 计算并填充CRC
        int crc = calculateCRC(frame, data.size());
        frame[data.size()] = (byte) (crc & 0xFF);
        frame[data.size() + 1] = (byte) ((crc >> 8) & 0xFF);
        return frame;
    }

    // 计算Modbus CRC16
    private static int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;

        for (int i = 0; i < length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) == 1) {
                    crc = (crc >> 1) ^ 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }

        return crc;
    }

    // 校验接收数据的CRC
    public static boolean checkCRC(byte[] data) {
        if (data.length < 2) return false;

        int length = data.length - 2;
        int crc = calculateCRC(data, length);

        // 比较计算的CRC和接收的CRC
        return ((data[length] & 0xFF) == (crc & 0xFF)) &&
                ((data[length + 1] & 0xFF) == ((crc >> 8) & 0xFF));
    }

    // 以16进制格式打印字节数组
    public static void printHex(byte[] data) {
        for (byte b : data) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // 获取并打开COM4 (原代码中写的是COM4不是COM3)
        SerialPort comPort = SerialPort.getCommPort("COM4");
        comPort.setBaudRate(9600);
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        comPort.setParity(SerialPort.NO_PARITY);

        if (comPort.openPort()) {
            System.out.println("COM4 打开成功");

            /*// 创建Modbus RTU消息 (示例: 读取保持寄存器)
            byte[] message = createModbusMessage(0x01, 0x03, 0x00, 0x00, 0x00, 0x02);
            // 显示发送的16进制数据
            System.out.print("发送: ");
            printHex(message);
            // 发送数据
            comPort.writeBytes(message, message.length);*/

            // 接收数据
            while (true) {
                if (comPort.bytesAvailable() > 0) {
                    byte[] readBuffer = new byte[comPort.bytesAvailable()];
                    int numRead = comPort.readBytes(readBuffer, readBuffer.length);

                    // 显示接收的16进制数据
                    System.out.print("接收: ");
                    printHex(readBuffer);

                    // 校验CRC
                    if (checkCRC(readBuffer)) {
                        System.out.println("CRC校验通过");

                         // 创建Modbus RTU消息 (示例: 读取保持寄存器)
                        byte[] message = createModbusMessage(0x01, 0x03, 0x00, 0x00, 0x00, 0x02);
                        // 显示发送的16进制数据
                        System.out.print("发送: ");
                        printHex(message);
                        // 发送数据
                        comPort.writeBytes(message, message.length);

                    } else {
                        System.out.println("CRC校验失败!");
                    }
                }
//                try { Thread.sleep(100); } catch (Exception e) {}
            }
        } else {
            System.err.println("无法打开COM4");
        }
    }


}
