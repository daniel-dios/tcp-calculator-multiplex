package client.operation;

public interface Operation {
    String toReadableFormat();

    byte[] encode();
}
