package requestresult;

public class EmptyResult extends Result {
    public EmptyResult() { }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmptyResult;
    }
}
