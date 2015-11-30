stage 'Build'
node('master') {
     git url: 'https://github.com/Diabol/jenkins-demo.git'
     echo 'compiling project....'
     sh "echo 'running unit tests....'"
     sh "echo 'packaging and archiving artifacts....'"
     sh "sleep 5"
}

stage name: "Integrations Test", concurrency: 1
node {
    parallel(ci: {
        deploy hostname: 'team.a.ci.internal', type: 'CI', credentials: 'team-a-deploy', component: 'user-service'
        selenium hostanme: 'ci.internal', type: 'CI', executors: 1, root: 'test/selenium', include: 'FeatureTest.*', exclude: ''
     }, sonar: {
        sh "echo 'Running static code analysis with sonar...'"
        sh "sleep 5"
     })
}

stage name: 'QA', concurrency: 1
input message: 'Deploy to QA?', parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Re-configure the environment?', id: 'configure']]
checkpoint('Before QA')
parallel(deploy: {
    node {
        if (configure == true) {
          configure(hostname: 'qa1.internal', type: 'QA', credentials: 'team-a-deploy')
        }
        deploy hostname: 'team.a.qa.internal', type: 'QA1', credentials: 'team-a-deploy', component: 'user-service'
        selenium hostanme: 'qa.internal', type: 'QA', executors: 1, root: 'test/selenium', include: 'SmokeTest.*', exclude: ''
    }
}, releaseNotes: {
   node {
       sh "echo 'Generating Release Notes...'"
   }
})

stage name: 'STAGE', concurrency: 1
input message: 'Deploy to STAGE?', parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Run performance tests?', id: 'performance']]
checkpoint('Before STAGE')
node {
     deploy hostname: 'team.a.stage.internal', type: 'STAGE', credentials: 'team-a-deploy', component: 'user-service'
     selenium hostanme: 'stage.internal', type: 'STAGE', executors: 1, root: 'test/selenium', include: 'SmokeTest.*', exclude: ''
     if (performance == true) {
       selenium hostanme: 'stage.internal', type: 'STAGE', executors: 100, root: 'test/selenium', include: 'PerformanceTest.*', exclude: ''
     }
}

stage name: 'PROD', concurrency: 1
input message: 'Deploy to PROD?', submitter: 'andreas'
checkpoint('Before PROD')
node('restricted-slave') {
     deploy hostname: 'team.a.prod.external', type: 'PROD', credentials: 'team-a-deploy', component: 'user-service'
     selenium hostanme: 'stage.internal', type: 'STAGE', executors: 1, root: 'test/selenium', include: 'SmokeTest.*', exclude: ''
}

def configure(params) {
  echo "Configuring $params.type environment on $params.hostname with redentials $params.credentials"
  sh "sleep 5"
}

def deploy(params) {
 echo "Deploying to $params.component to $params.type($params.hostname) with credentials $params.credentials"
 sh "sleep 5"
}

def selenium(params) {
 echo "Running $params.include selenium tests on $params.executors executors towards $params.type($params.hostname)from $params.root"
 sh "sleep 5"
}
