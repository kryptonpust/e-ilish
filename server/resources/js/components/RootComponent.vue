<template>
  <div class="root">
    <header-component></header-component>
    <notifications group="notification" position="top right"/>
    <div class="container is-fluid" id="admin">
      <div class="columns">
        <div class="column is-one-fifth">
          <side-bar></side-bar>
        </div>
        <div class="column">
          <router-view></router-view>
        </div>
      </div>
    </div>
    <balloon title="Messenger" :initiallyConcise="true">
      <chat-component></chat-component>
    </balloon>
  </div>
</template>
<script>
import io from "socket.io-client";
import ChatComponent from "./ChatComponent.vue";
import SideBar from "./SideBar.vue";
import HeaderComponent from "./HeaderComponent.vue";
import { Balloon } from "vue-balloon";
const ORIGIN_SERVER = 0;
export default {
  components: { HeaderComponent, SideBar, ChatComponent, Balloon },
  mounted() {
    var socket = io("http://e-ilish.org:3000/admin");
    // var socket = io("http://103.108.140.72:3000/admin");
    this.$root.$on(
      "smsg",
      function(msg, to) {
        socket.emit("message", JSON.stringify(msg), to);
      }.bind(this)
    );
    this.$root.$on(
      "simage",
      function(msg, to) {
        socket.emit("imagemsg", JSON.stringify(msg), to);
      }.bind(this)
    );
    this.$root.$on("update", function() {
      socket.emit("update");
    });
    this.$root.$on(
      "selected",
      function(val) {
        this.selected = val;
        socket.emit("reset_user_pending", this.selected);
      }.bind(this)
    ),
      this.$root.$on("delete", function(id) {
        socket.emit("delete", JSON.stringify({ data: id }));
      }),
      socket.on(
        "admin",
        function(msg, messenger_id) {
          var msg = JSON.parse(msg);
          this.$root.$emit("nmsg", msg, messenger_id);
          if (this.selected == messenger_id) {
            socket.emit("reset_user_pending", messenger_id);
          } else {
            this.$root.$emit("update_pending", messenger_id);
          }
          if (msg.origin) {
            var audio = new Audio("/sound.mp3");
            audio.play();
            this.$notify({
              group: "notification",
              type: "success",
              title: "New message",
              text: "A new Message received!!"
            });
          }
        }.bind(this)
      );
    socket.on(
      "reset_pending",
      function(id) {
        this.$root.$emit("reset_pending", id);
      }.bind(this)
    );
    socket.on(
      "refesh",
      function(msg) {
        var data = JSON.parse(msg);
        this.$root.$emit("refresh");
        if (data.song) {
          var audio = new Audio("/" + data.song);
          audio.play();
        }
        this.$notify({
          group: "notification",
          type: data.type,
          title: data.title,
          text: data.text
        });
      }.bind(this)
    );
    socket.on(
      "log",
      function(msg) {
        this.$root.$emit("nlog", JSON.parse(msg));
      }.bind(this)
    );
    socket.on("error", error => {
      this.$root.$emit("nlog", error);
      //console.log(error);
    });
    socket.on("connect_error", error => {
      this.$root.$emit("nlog", error);
      this.$notify({
        group: "notification",
        type: "error",
        title: "Chat server Error",
        text: error
      });
      //console.log(error);
    });
  },
  data() {
    return {
      selected: null
    };
  },
  methods: {}
};
</script>
<style lang="scss" scoped>
.root {
  height: 100%;
  width: 100%;
}
</style>

