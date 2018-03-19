import ActivitiesList from './activities/list';
import ActivitiesEdit from './activities/edit';

const routes = [
  { path: '/', component: ActivitiesList },
  { path: '/activities/list', component: ActivitiesList },
  { path: '/activities/add', component: ActivitiesEdit },
  { path: '/activities/edit/:id', component: ActivitiesEdit }
]

export default routes;