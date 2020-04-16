<template>
  <div class="xterm"/>
</template>

<script>
import "xterm/dist/xterm.css";
import { Terminal } from "xterm";
import * as fit from "xterm/dist/addons/fit/fit";
// import props from './props'
// import {TerminalStream} from './terminal-stream'

Terminal.applyAddon(fit);

export default {
  mounted() {
    let term = new Terminal({
      useStyle: true,
      screenKeys: true
    });
    term.open(this.$el, true);
    // term.write("\x1B[1;3;31mLog\x1B[0m $ ");
    this.$root.$on(
      "nlog",
      function(msg) {
        term.write("\x1B[1;3;31mLog\x1B[0m $ ");
        term.writeln(msg);
      }.bind(this)
    );
    term.on("data", function(data) {});
  }
};
</script>
