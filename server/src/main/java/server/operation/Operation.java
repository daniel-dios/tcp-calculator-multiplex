package server.operation;

public interface Operation {

    Result solve();

    String toReadableFormat();
}
