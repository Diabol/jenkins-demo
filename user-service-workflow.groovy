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
        deploy 'CI'
        selenium 'CI'
     }, sonar: {
        sh "echo 'Running static code analysis with sonar...'"
        sh "sleep 5"
     })
}

stage name: 'QA', concurrency: 1
input message: 'Deploy to QA?', parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Re-create the environment?']]
checkpoint('Before QA')
parallel(deploy: {
    node {
        deploy 'QA1'
        selenium 'QA1'
    }
}, releaseNotes: {
   node {
       sh "echo 'Generating Release Notes...'"
   }
})
input message: 'Did manual test pass? ', ok: 'Yes'

stage name: 'STAGE', concurrency: 1
input message: 'Deploy to STAGE?', parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Re-load production data?']]
checkpoint('Before STAGE')
node {
     deploy 'STAGE'
}

stage name: 'PROD', concurrency: 1
input message: 'Deploy to PROD?', submitter: 'andreas'
checkpoint('Before PROD')
node {
     deploy 'PRODUCTION'
}

def deploy(environment) {
 sh "echo 'Deploying to $environment'"
 sh "sleep 5"
}

def selenium(environment) {
 sh "echo 'Running tests on $environment'"
 sh "sleep 5"
}
