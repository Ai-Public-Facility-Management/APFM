import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/users',
      component: () => import('../components/ui/UsersGrid.vue'),
    },
    {
      path: '/cameras',
      component: () => import('../components/ui/CameraGrid.vue'),
    },
    {
      path: '/inspections',
      component: () => import('../components/ui/InspectionGrid.vue'),
    },
    {
      path: '/publicFas',
      component: () => import('../components/ui/PublicFaGrid.vue'),
    },
    {
      path: '/proposals',
      component: () => import('../components/ui/ProposalGrid.vue'),
    },
    {
      path: '/reports',
      component: () => import('../components/ui/ReportGrid.vue'),
    },
    {
      path: '/resultReports',
      component: () => import('../components/ui/ResultReportGrid.vue'),
    },
    {
      path: '/issues',
      component: () => import('../components/ui/IssueGrid.vue'),
    },
  ],
})

export default router;
