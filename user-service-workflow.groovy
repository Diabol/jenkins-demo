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
          configure hostname: 'qa1.internal' type: 'QA', credentials: 'team-a-deploy' component: 'user-service'
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

def deploy(hostname,type,credentials,component) {
  echo "Configuring $type environment on $hostname with redentials $credentials"
  sh "sleep 5"
}

def deploy(hostname,type,credentials,component) {
 echo "Deploying to $component to $type($hostname) with credentials $credentials"
 sh "sleep 5"
}

def selenium(hostname, type, executors, root, include, exclude) {
 echo "Running $include selenium tests on $executors executors towards $type($hsotname)from $root"
 sh "sleep 5"
}
