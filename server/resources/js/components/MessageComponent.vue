<template>
  <div class="msg" v-if="load==null">Select a user to see messages</div>
  <div class="spinner" v-else-if="loading"></div>
  <div v-else class="chats" v-chat-scroll="{always: false, smooth: true, scrollonremoved:true}">
    <ul>
      <li v-for="d in data" v-bind:key="d.ID" :class="{'server': !d.origin}">
        <div>
          <img
            :src="d.message"
            v-if="d.msg_type=='image'"
            style="width:300px;height:300px;"
            @click="showmodal(d.message)"
          >
          <span v-else class="chat-container">{{d.message}}</span>
        </div>
      </li>
    </ul>
    <div class="modal is-active" v-if="showmodel">
      <div class="modal-background"></div>
      <div class="modal-content">
        <div class>
          <img :src="imgsrc">
        </div>
      </div>
      <button
        class="modal-close is-large"
        aria-label="close"
        @click="showmodel = false; imgsrc= ''"
      ></button>
    </div>
  </div>
</template>

<script>
import * as notihelper from "../NotifiactionHandler.js";

export default {
  props: ["load"],
  mounted() {
    this.$root.$on(
      "nmsg",
      function(msg, id) {
        if (this.data && this.load == id) {
          this.data.push(msg);
        }
      }.bind(this)
    );
  },

  watch: {
    load: function(newval, oldval) {
      if (newval != null) {
        this.loading = true;
        this.fetchchats();
      }
    }
  },
  data() {
    return {
      data: null,
      loaded: this.load,
      loading: false,
      showmodel: false,
      imgsrc: ""
    };
  },
  methods: {
    showmodal(x) {
      this.imgsrc = x;
      this.showmodel = true;
    },
    fetchchats() {
      axios
        .get("/api/getchats/" + this.load)
        .then(data => {
          this.data = data.data;
          this.loading = false;
        })
        .catch(err => {
          notihelper.noti_error.call(this, err);
        });
    }
  }
};
</script>

<style lang="scss" scoped>
.chats {
  width: 100%;
  overflow-y: auto;
  li {
    display: flex;
    margin: 10px;
    &.server {
      justify-content: flex-end;
      .chat-container {
        background-color: #95bd6b99;
      }
    }
  }
  .chat-container {
    background-color: #e7dcdce7;
    border-radius: 1.3em;
    padding: 5px;
  }
}
.modal-content {
  display: flex;
  align-items: center;
  justify-content: center;
}
.msg {
  flex-grow: 0.5;
}
</style>
