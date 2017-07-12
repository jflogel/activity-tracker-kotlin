const ActivitiesList = {
    template: `<div>
        <div v-if="activityStats"> Week total: {{activityStats.totalForWeek}}</div>
        <div v-if="activityStats"> Year total: {{activityStats.totalForYear}}</div>
        <div v-if="activityStats"> Weekly average: {{activityStats.average}}</div>
        <div id="columnchart_values" style="height: 400px;"></div>
        <table v-if="activities" class="table table-striped table-condensed table-hover activities">
            <tr v-for="activity in activities" class="row">
                <td format="moment(date).format ~ dddd, M/D, h:mm a">{{activity.date | formatDate}}</td>
                <td>{{activity.activity}}</td>
                <td>{{activity.time_duration}} minutes</td>
                <td>{{activity.distance}} {{activity.distance_units}}</td>
                <td>
                    <router-link v-bind:to="'/activities/edit/' + activity.id" class="edit" title="Edit">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </router-link>
                    <button class="delete" v-on:click="deleteActivity(activity)" title='Delete'>
                        <span class="glyphicon glyphicon-trash"></span>
                    </button>
                </td>
            </tr>
        </table>
    </div>`,
    data: function () {
        return {
            activityStats: null,
            activities: []
        };
    },
    created: function() {
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(this.fetchData);
    },
    methods: {
     fetchData: function () {
       const activity = this.$route.query.activity || '';
       var xhr = new XMLHttpRequest()
       var self = this
       xhr.open('GET', '/data/activities/chart?activity=' + activity)
       xhr.onload = function () {
            var dataTableData = [];
            const json = JSON.parse(xhr.responseText);
            self.activityStats = json.activityStats;
            self.activities = json.activities;
            dataTableData.push(json.legendValues);
            json.summaries.forEach(function(summary) {
                dataTableData.push([summary.date, summary.running, summary.core, summary.swimming, summary.weights]);
            });
            self.chartActivities = json;

            var data = google.visualization.arrayToDataTable(dataTableData);
            var view = new google.visualization.DataView(data);

            var options = {
                height: 400,
                legend: { position: 'top', maxLines: 3 },
                bar: { groupWidth: '75%' },
                isStacked: true
            };
            var chart = new google.visualization.ColumnChart(document.getElementById("columnchart_values"));
            chart.draw(view, options);
       }
       xhr.send()
     },
     deleteActivity: function (activity) {
        var xhr = new XMLHttpRequest()
        var self = this
        xhr.open('DELETE', '/activity/delete?id=' + activity.id)
        xhr.onload = function () {
            if (xhr.status == 200) {
                self.fetchData();
            } else {
                console.log('something went wrong: ', xhr.response)
            }
        }
        xhr.send();
     }
    },
    filters: {
        formatDate: function (epoch) {
            return moment.unix(epoch).format('dddd, M/D, h:mm a');
        }
      }
}
const ActivitiesEdit = { template:
    `<div>
        <ul v-if="errors" class="list-unstyled alert alert-danger">
            <li v-for="error in errors">{{error}}</li>
        </ul>
        <form class="form-horizontal" v-on:submit.prevent v-bind:action="model.id ? '/activities/edit/' + model.id : '/activities/add'" method='POST'>
            <div class="form-group">
                <label class="control-label col-sm-1" for="date">Date:</label>
                <div class="col-sm-3">
                    <input class="form-control" id='date' name='date' type='datetime-local' placeholder='mm/dd/yyyy, hh:mm AM' v-model="model.date">
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='activity'>Activity:</label>
                <div class="col-sm-3">
                    <select class="form-control col-sm-1" id='activity' v-model="model.activity" name='activity'>
                        <option v-for="activity in activities" v-bind:value="activity.description">{{activity.description}}</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='duration'>Duration:</label>
                <div class="col-sm-3">
                    <input class="form-control col-sm-1" id='duration' name='duration' min='0' type='number' placeholder='minutes' v-model="model.time_duration">
                </div>
                <div class="col-sm-1">minutes</div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-1" for='distance'>Distance:</label>
                <div class="col-sm-3">
                    <input class="form-control col-sm-1" id='distance' name='distance' type='text' v-model="model.distance">
                </div>
                <div class="col-sm-1">
                    <select class="form-control col-sm-1" v-model="model.distance_units" name='distance_units'>
                        <option value='miles'>miles</option>
                        <option value='meters'>meters</option>
                        <option value='yards'>yards</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button class="btn btn-default col-sm-1 col-sm-offset-1" type='submit' v-on:click="saveData">Save</button>
                <router-link v-bind:to="createCancelLink(model)" class="btn btn-default col-sm-1">Cancel</router-link>
            </div>
        </form>
    </div>`,
    data: function() {
        return {
            errors: null,
            activities: [],
            model: {
                activity: 'Running',
                date: this.defaultDate(),
                distance_units: 'miles',
                time_duration: 0
            }
        };
    },
    created: function() {
        this.fetchData();
    },
    methods: {
        formatDate: function(date) {
            return moment.unix(date).format('YYYY-MM-DDTHH:mm')
        },
        defaultDate: function() {
            return moment().format('YYYY-MM-DDT00:00');
        },
        fetchData: function () {
           const activity = this.$route.params.id || '';
           var xhr = new XMLHttpRequest()
           var self = this
           xhr.open('GET', '/activity/edit?activity=' + activity)
           xhr.onload = function () {
                const json = JSON.parse(xhr.responseText);
                self.activities = json.activities;
                if (json.model) {
                    self.model = Object.assign({}, json.model, {
                        date: self.formatDate(json.model.date)
                    });
                }
           }
           xhr.send()
         },
         saveData: function (event) {
            var xhr = new XMLHttpRequest()
            var self = this
            xhr.open('POST', '/activity/save')
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onload = function () {
                const response = JSON.parse(xhr.responseText);
                if (response.status >= 400) {
                    self.errors = [response.message];
                } else if (response.errors) {
                    self.errors = response.errors;
                } else {
                    self.$router.push('/activities/list?activity=' + self.model.activity.toLowerCase());
                }
            }
            var requestBody = Object.assign({}, this.model, {
                date: moment(this.model.date, 'YYYY-MM-DDTHH:mm').unix()
            });
            xhr.send(JSON.stringify(requestBody));
         },
         createCancelLink: function (model) {
            if (model.id && model.activity) {
                return '/activities/list?activity=' + model.activity;
            }
            return '/activities/list';
         }
    }
}

const routes = [
  { path: '/', component: ActivitiesList },
  { path: '/activities/list', component: ActivitiesList },
  { path: '/activities/add', component: ActivitiesEdit },
  { path: '/activities/edit/:id', component: ActivitiesEdit }
]

const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active'
})

const app = new Vue({
    router
}).$mount('#app')