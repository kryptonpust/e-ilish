<template>
  <div class="box">
    <div class="space-between">
      <div class="title" style="color: red">Save and Edit Warning Message:</div>
      <div class="field">
        <input
          id="switchExample"
          type="checkbox"
          name="switchExample"
          class="switch"
          v-model="data.enable"
        >
        <label for="switchExample">ON/OFF</label>
      </div>
    </div>
    <div class="option">
      <div class="color-style">
        <strong class>Text color:</strong>
        <Swatches show-fallback v-model="data.txtcolor" colors="material-basic"></Swatches>
      </div>
      <div class="color-style">
        <strong class>Text background color:</strong>
        <Swatches show-fallback v-model="data.bgcolor" colors="material-light" popover-to="left"></Swatches>
      </div>
    </div>
    <div class="is-divider" data-content="Type Info"></div>
    <textarea
      class="textarea"
      v-model="data.infotxt"
      placeholder="Enter Info"
      :style="{'color': data.txtcolor,'background-color': data.bgcolor}"
    ></textarea>
    <a class="button is-large is-primary" @click="updateinfo">
      <span class="icon is-medium">
        <i class="fas fa-cloud"></i>
      </span>
      <span>Save</span>
    </a>
  </div>
</template>

<script>
import Swatches from "vue-swatches";
import "vue-swatches/dist/vue-swatches.min.css";
import * as notihelper from "../NotifiactionHandler.js";

export default {
  components: { Swatches },
  mounted() {
    this.fetchdata();
  },
  data() {
    return {
      data: {}
    };
  },
  methods: {
    fetchdata() {
      axios
        .get("/api/getinfo")
        .then(data => {
          this.data = data.data[0];
        })
        .catch(err => {
          notihelper.noti_error.call(this, err);
        });
    },
    updateinfo() {
      if (this.data.txtcolor.length >= 6 && this.data.bgcolor.length >= 7) {
        axios
          .post("/api/updateinfo", this.data, {
            headers: { "content-type": "application/json" }
          })
          .then(data => {
            // console.log(data.data);
            //console.log(data);
            this.$root.$emit("update");
            notihelper.notification.call(this, data);
          })
          .catch(error => {
            notihelper.noti_error.call(this, error);
          });
      } else {
        notihelper.noti_warn.call(this, "Color must be 6 digit String");
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.box {
  display: flex;
  flex-direction: column;
  flex: 6;
}
.color-style {
  display: flex;
  flex: 1;
  justify-content: space-between;
  margin: 20px;
  background-color: #f5f5f5;
  padding: 5px;
  align-items: center;
  border-radius: 10px;
  strong {
    padding-bottom: 10px;
    font-size: 2rem;
  }
}
.space-between {
  display: flex;
  flex: 1;
  justify-content: space-between;
  margin: 20px;
  // background-color: antiquewhite;
}
.option {
  display: flex;
  flex: 2;
}
.button {
  margin: 10px;
}
</style>
