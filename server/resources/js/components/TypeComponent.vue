<template>
  <div class="hr-line">
    <div class="newmsg">
      <textarea
        v-model="nmessage"
        class="msg"
        placeholder="Type here..."
        @keydown.enter.exact.prevent="send"
      ></textarea>
      <label class="custom-file-upload">
        <input type="file" accept="image/*" ref="uploadfiles" @change="filehandler">
        <i class="fas fa-file-image fa-2x"></i>
      </label>
      <!-- <form enctype="multipart/form-data" class="image">
        <i class="fas fa-image"></i>
        <input type="file" accept="image/*">
      </form>-->
      <i class="fas fa-paper-plane send fa-2x" @click="send"></i>
      <!-- <a class="send" @click="send">Send</a> -->
    </div>
  </div>
</template>

<script>
export default {
  props: ["type"],
  data() {
    return {
      nmessage: null
    };
  },
  methods: {
    filehandler() {
      // console.log(this.$refs.uploadfiles.files[0]);
      var reader = new FileReader();
      reader.readAsDataURL(this.$refs.uploadfiles.files[0]);
      reader.onload = function() {
        this.$root.$emit(
          "simage",
          {
            message: reader.result,
            msg_type: "image",
            origin: 0
          },
          this.type
        );
        // console.log(reader.result);
      }.bind(this);
      reader.onerror = function(error) {
        console.log("Error: ", error);
      };

      // console.log(getBase64(this.$refs.uploadfiles.files[0]));
    },
    send() {
      if (this.nmessage != null) {
        this.$root.$emit(
          "smsg",
          { message: this.nmessage, msg_type: "text", origin: 0 },
          this.type
        );
        this.nmessage = null;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.newmsg {
  flex-basis: 8%;
  display: flex;
  padding: 10px;
  align-items: center;
  justify-content: flex-end;
  .send {
    color: #c0e48e;
    margin: 3px;
    cursor: pointer;
  }
  .send:hover {
    color: red;
  }
  .image {
    font-size: 1.8em;
    margin: 3px;
  }
}
.msg {
  margin-right: 10px;
  border: none;
  outline: none;
  resize: none;
  flex-grow: 1;
}
.hr-line {
  width: 100%;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 20 !important;
}
input[type="file"] {
  display: none;
}
.custom-file-upload {
  display: inline-block;
  cursor: pointer;
  color: #38d2cd;
}
.custom-file-upload:hover {
  color: red;
}
</style>
