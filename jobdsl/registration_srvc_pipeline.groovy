def scmUrl = 'https://github.com/Diabol/jenkins-demo.git'
def sleepTime = 3

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
                url(scmUrl)
            }
        }
    }
    triggers {
        scm('* * * * *')
    }
    wrappers {
        deliveryPipelineVersion('1.0.0.\$BUILD_NUMBER', true)
    }

    steps {
        shell("""
          ./unitTest.sh
          sleep $sleepTime
        """)
    }

    publishers {
        archiveJunit('test-reports/*.xml')
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
                url(scmUrl)
            }
        }
    }

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell("sleep $sleepTime")
    }

    publishers {
        downstreamParameterized {
            trigger('Registration/DeployCI') {
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
                url(scmUrl)
            }
        }
    }

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell("sleep $sleepTime")
    }
}

job('Registration/DeployCI') {
    deliveryPipelineConfiguration("Integration", "Deploy to CI")
    scm {
      git {
          remote {
              url(scmUrl)
          }
      }
    }
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("""
          ./deployCI.sh
          sleep $sleepTime
          """)
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
    scm {
      git {
          remote {
              url(scmUrl)
          }
      }
    }
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("""
          ./integrationTest.sh
          sleep $sleepTime
        """)
    }
    publishers {
      archiveJunit('test-reports/*.xml')
      downstreamParameterized {
          trigger('Registration/Test2CI') {
            condition('SUCCESS')
            triggerWithNoParameters(true)
          }
      }
    }
}

job('Registration/Test2CI') {
    deliveryPipelineConfiguration("Integration", "Extended functional tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("sleep $sleepTime")
    }
    publishers {
      downstreamParameterized {
          trigger('Registration/Test3CI') {
            condition('SUCCESS')
            triggerWithNoParameters(true)
          }
      }
    }
}

job('Registration/Test3CI') {
    deliveryPipelineConfiguration("Integration", "End-to-end tests")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("sleep $sleepTime")
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
        shell("sleep $sleepTime")
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
        shell("sleep $sleepTime")
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
        shell("sleep $sleepTime")
    }
}

job('Registration/GenerateReleaseNotes') {
    deliveryPipelineConfiguration("Acceptance", "Generate Relese Notes")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("sleep $sleepTime")
    }
}

job('Registration/DeployProd') {
    deliveryPipelineConfiguration("Production", "Deploy to PROD")
    wrappers {
        buildName('\$PIPELINE_VERSION')
    }
    steps {
        shell("sleep $sleepTime")
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
        shell("sleep $sleepTime")
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
        shell("sleep $sleepTime")
    }
}
