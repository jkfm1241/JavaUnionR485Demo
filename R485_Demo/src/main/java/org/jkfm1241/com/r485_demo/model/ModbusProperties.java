package org.jkfm1241.com.r485_demo.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "custom.modbus.request")
public class ModbusProperties {

    private List<Integer> command; // 直接绑定YAML列表

    // 如果使用字符串格式（如"0x01, 0x03..."），需额外处理
    private String commandString;

    // Getter/Setter
    public List<Integer> getCommand() {
        return command;
    }

    public void setCommand(List<Integer> command) {
        this.command = command;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }

}
