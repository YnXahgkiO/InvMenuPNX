package back.invmenupnx;

@FunctionalInterface
public interface SendCallback {

    void onSend(boolean success);
}
