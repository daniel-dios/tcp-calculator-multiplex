package server.operation;

public class Result {

    public final boolean success;
    public final long result;
    public final String reason;

    private Result(
            final boolean success,
            final long result,
            final String reason
    ) {
        this.success = success;
        this.result = result;
        this.reason = reason;
    }

    public static Result success(
            final long result
    ) {
        return new Result(true, result, "success");
    }

    public static Result failure(
            final String error
    ) {
        return new Result(false, 0, error);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Result result1 = (Result) o;

        if (success != result1.success) return false;
        if (result != result1.result) return false;
        return reason.equals(result1.reason);
    }

    @Override
    public int hashCode() {
        int result1 = (success ? 1 : 0);
        result1 = 31 * result1 + (int) (result ^ (result >>> 32));
        result1 = 31 * result1 + reason.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return success ? String.valueOf(result) : reason;
    }
}
