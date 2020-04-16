<template>
  <div class="box">
    <!-- <aside class="menu">
      <div class="header">Pages</div>
      <ul class="menu-list">
        <li>
          <router-link tag="a" to="/new">New Page</router-link>
        </li>
        <li>
          <router-link tag="a" to="/pages">Show Pages</router-link>
        </li>
        <li>
          <router-link tag="a" to="/infotext">Warning Message</router-link>
        </li>
      </ul>
      <p class="menu-label">Schedule Notification</p>
      <ul class="menu-list">
        <li>
          <router-link tag="a" to="/addnotifications">Add Notification</router-link>
        </li>
      </ul>
      <p class="menu-label">Administration</p>
      <ul class="menu-list">
        <li>
          <router-link tag="a" to="/logging">Live Log</router-link>
        </li>
        <li>
          <router-link tag="a" to="/users">Users</router-link>
        </li>
        <li>
          <a href="/logout" class="logout">Logout</a>
        </li>
      </ul>
    </aside>-->
    <aside class="menu">
      <div v-for="(d,index) in data" v-bind:key="index">
        <div class="header" v-if="d.show" @click="d.show=false">
          <p>{{d.name}}</p>
          <i class="fas fa-minus-circle"></i>
        </div>
        <div v-else class="header" @click="d.show=true">
          <p>{{d.name}}</p>
          <i class="fas fa-plus-circle"></i>
        </div>
        <ul class="menu-list" v-if="d.show">
          <li v-for="(c,cindex) in d.child" v-bind:key="index+'-'+cindex">
            <router-link v-if="c.path!='/logout'" tag="a" :to="c.path">{{c.name}}</router-link>
            <a v-else href="/logout" class="logout">Logout</a>
          </li>
        </ul>
      </div>
    </aside>
  </div>
</template>

<script>
// {
//   name: "",
//   child: [{ path: "", name: "" }]
// }
export default {
  data() {
    return {
      data: [
        {
          name: "pages",
          show: false,
          child: [
            { path: "/new", name: "New Page" },
            { path: "/pages", name: "Show Pages" },
            { path: "/infotext", name: "Warning Message" }
          ]
        },
        {
          name: "Schedule Notification",
          show: false,
          child: [{ path: "/addnotifications", name: "Add Notification" }]
        },
        {
          name: "Administration",
          show: false,
          child: [
            { path: "/logging", name: "Live Log" },
            { path: "/users", name: "Users" },
            { path: "/logout", name: "Logout" }
          ]
        }
      ]
    };
  }
};
</script>

<style lang="scss" scoped>
.logout:hover {
  background-color: #f33535c0;
  color: white;
  font-size: 1.2rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  border-radius: 2px;
  color: #4a4a4a;
  padding: 2px;
  text-transform: uppercase;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
  font-weight: bold;
  margin: 5px;
  color: tomato;
  &:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }
  .menu-list {
    background-color: #edf5f2e6;
    z-index: -1;
  }
}
</style>
