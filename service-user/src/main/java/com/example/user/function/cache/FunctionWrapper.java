package com.example.user.function.cache;


import groovy.lang.Script;
import lombok.Getter;

import java.util.Map;

public class FunctionWrapper {

    private final String functionCode;
    @Getter
    private final long version;
    private final Script compiledScript;

    public FunctionWrapper(String functionCode, long version, Script compiledScript) {
        this.functionCode = functionCode;
        this.version = version;
        this.compiledScript = compiledScript;
    }

    public Object execute(Map<String, Object> params) {
        params.forEach(compiledScript::setProperty);
        return compiledScript.run();
    }

    public Script getScript() {
        return compiledScript;
    }
}
