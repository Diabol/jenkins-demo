folder('Registration')

deliveryPipelineView('Registration/Pipeline') {

    //showAggregatedPipeline()
    pipelineInstances(5)
    enableManualTriggers()
    sorting(Sorting.TITLE)
    showChangeLog()
    showAvatars()
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
              triggerWithNoParameters(true)
            }
            trigger('Registration/Package') {
              condition('SUCCESS')
              triggerWithNoParameters(true)
            }
        }
    }
}

job('Registration/Package') {
    deliveryPipelineConfiguration("Build", "Package & archive")
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

    publishers {
        downstreamParameterized {
            trigger('Registration/Package') {
              condition('SUCCESS')
              triggerWithNoParameters(true)
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
            trigger('Registration/Test1CI') {
              condition('SUCCESS')
              triggerWithNoParameters(true)
            }
        }
    }
}

job('Registration/Test1CI') {
    deliveryPipelineConfiguration("Integration", "Basic functional tests")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }


    publishers {
        buildPipelineTrigger('Registration/Test2CI') {
        }
    }
}

job('Registration/Test2CI') {
    deliveryPipelineConfiguration("Integration", "Extended functional tests")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell(
                'sleep 10'
        )
    }

    publishers {
        buildPipelineTrigger('Registration/Test3CI') {
        }
    }
}

job('Registration/Test3CI') {
    deliveryPipelineConfiguration("Integration", "End-to-end tests")

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
              triggerWithNoParameters(true)
            }
            trigger('Registration/TestQA') {
              condition('SUCCESS')
              triggerWithNoParameters(true)
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
        buildPipelineTrigger('Registration/DeployProd, Registration/TestPerformanceQA') {
        }
    }
}

job('Registration/TestPerformanceQA') {
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
              triggerWithNoParameters(true)
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

    publishers {
        downstreamParameterized {
            trigger('Registration/PublishReleaseNotes') {
              condition('SUCCESS')
              triggerWithNoParameters(true)
            }
        }
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
