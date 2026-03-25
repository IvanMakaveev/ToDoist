package bg.sofia.uni.fmi.mjt.todoist.server.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandCreatorTest {
    @Test
    void testNewCommandWorksCorrectly() {
        String testCommand = "commandName testargs1 testargs2 group\"test test\"";
        Command result = CommandCreator.newCommand(testCommand);
        assertEquals("commandName", result.name(), "Result command name should match");
        assertEquals("testargs1", result.arguments()[0], "Result command args[0] should match");
        assertEquals("testargs2", result.arguments()[1], "Result command args[1] should match");
        assertEquals("grouptest test", result.arguments()[2], "Result command args[2] should match");
        assertEquals(3, result.arguments().length, "Result command args size should be correct");
    }
}
