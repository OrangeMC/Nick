package moe.orangemc.nick.skin;

import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.nick.api.SkinSource;

public class SkinPropertyImpl implements SkinProperty {
    private final String value;
    private final String signature;
    private final SkinSource source;

    public SkinPropertyImpl(String value, String signature, SkinSource source) {
        this.value = value;
        this.signature = signature;
        this.source = source;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    public SkinSource getSource() {
        return source;
    }
}
