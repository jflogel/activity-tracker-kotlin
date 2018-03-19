import {formatDuration} from '../utilities/duration-helper';
import moment from 'moment';

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
                <td>{{activity.time_duration | formatDuration(activity.time_duration_units)}}</td>
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
        },
        formatDuration: formatDuration
      }
}

export default ActivitiesList;