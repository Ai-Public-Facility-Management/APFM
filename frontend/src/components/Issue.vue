<template>
    <div>
        <Number
            label="PublicFaId"
            v-model="value.publicFaId"
            :editMode="editMode"
        />
        <Number
            label="ResultId"
            v-model="value.resultId"
            :editMode="editMode"
        />
        <Number
            label="ProposalId"
            v-model="value.proposalId"
            :editMode="editMode"
        />
        <Date
            label="CreationDate"
            v-model="value.creationDate"
            :editMode="editMode"
        />
        <String
            label="Estimate"
            v-model="value.estimate"
            :editMode="editMode"
        />
        <type
            offline
            label="Type"
            v-model="value.type"
            :editMode="editMode"
            @change="change"
        />
        <Photo
            offline
            label="Image"
            v-model="value.image"
            :editMode="editMode"
            @change="change"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'Issue',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "issues",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
