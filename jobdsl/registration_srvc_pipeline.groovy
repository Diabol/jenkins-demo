folder('Registration')

deliveryPipelineView('Registration/Pipeline') {

    pipelineInstances(5)
    enableManualTriggers()
    showChangeLog()
    pipelines {
        component('Component', 'Registration/Build')
    }

}

job('Registration/Build') {
    deliveryPipelineConfiguration("Build", "Build & unit test")
    scm {
        git {
            remote {
                url('https://github.com/Diabol/dummy.git')
            }
        }
    }
    wrappers {
        deliveryPipelineVersion('1.0.0.\$BUILD_NUMBER', true)
    }
    publishers {
        downstreamParameterized {
            trigger('Registration/Sonar') {
            }
            trigger('Registration/DeployCI') {
              condition('SUCCESS')
            }
        }
    }
}

job('Registration/Sonar') {
    deliveryPipelineConfiguration("Build", "Sonar analysis")
    scm {
        git {
            remote {
                url('https://github.com/Diabol/dummy.git')
            }
        }
    }

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }
}

job('Registration/DeployCI') {
    deliveryPipelineConfiguration("Integration", "Deploy to CI")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 5'
        )
    }

    publishers {
        downstreamParameterized {
            trigger('Registration/TestCI') {
              condition('SUCCESS')
            }
        }
    }
}

job('Registration/TestCI') {
    deliveryPipelineConfiguration("Integration", "Functional test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }


    publishers {
        buildPipelineTrigger('Registration/DeployQA') {
        }
    }
}

job('Registration/DeployQA') {
    deliveryPipelineConfiguration("Acceptance", "Deploy to QA")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 5'
        )
    }

    publishers {
        downstreamParameterized {
            trigger('Registration/GenerateReleaseNotes') {
              condition('SUCCESS')
            }
            trigger('Registration/TestQA') {
              condition('SUCCESS')
            }
        }
    }
}

job('Registration/TestQA') {
    deliveryPipelineConfiguration("Acceptance", "Smoke test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }


    publishers {
        buildPipelineTrigger('Registration/DeployProd') {
        }
        buildPipelineTrigger('Registration/TestPerfromanceQA') {
        }
    }
}

job('Registration/TestPerfromanceQA') {
    deliveryPipelineConfiguration("Acceptance", "Performance test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 20'
        )
    }
}

job('Registration/GenerateReleaseNotes') {
    deliveryPipelineConfiguration("Acceptance", "Generate Relese Notes")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }
}

job('Registration/DeployProd') {
    deliveryPipelineConfiguration("Production", "Deploy to PROD")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 5'
        )
    }

    publishers {
        downstreamParameterized {
            trigger('Registration/TestProd') {
              condition('SUCCESS')
            }
        }
    }
}

job('Registration/TestProd') {
    deliveryPipelineConfiguration("Production", "Smoke test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 5'
        )
    }

}

job('Registration/PublishReleaseNotes') {
    deliveryPipelineConfiguration("Production", "Publish Relese Notes")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }
}
