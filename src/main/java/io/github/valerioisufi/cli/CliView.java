package io.github.valerioisufi.cli;

public interface CliView {

    CliView execute(CliEngine engine);

    void stop();

}
