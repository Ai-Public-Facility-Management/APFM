<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: 'Backend' }" />
        <VerticalNavLink
            :item="{
                title: 'Users',
                to: '/users',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Camera',
                to: '/cameras',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Inspection',
                to: '/inspections',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'PublicFa',
                to: '/publicFas',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Proposal',
                to: '/proposals',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Report',
                to: '/reports',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Result_report',
                to: '/resultReports',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'Issue',
                to: '/issues',
            }"
        />
    </ul>
</template>
