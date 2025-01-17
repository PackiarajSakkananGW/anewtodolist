import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.05"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        password("env.CONFIGURATION", "", label = "Properties", description = """Valid Syntax: Key="value" pairs, one pair per line. Maximum number of allowed characters are 4K""", display = ParameterDisplay.PROMPT)
        text("env.CONFIGURATION_COUNT", "", label = "Count", description = "Number of configuration parameter to be update.", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("env.DENY_LIST_LOCATION", "s3://atmos-in-dev-teamcity-artifacts/tcvs-planetdeployments/assets/in-dev/allowed-parameter.json", label = "Deny List Location", description = "Location of deny list", display = ParameterDisplay.HIDDEN, readOnly = true, allowEmpty = false)
        text("env.GIT_REPOSITORY", "git@github.com:iscs/insurancenow-core.git?ref=main", label = "Git repository URL", description = "Repository location of config.properties file with branch name", allowEmpty = false)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "TestBuildParamCMDLIne"
            scriptContent = """
                python3.9 /Users/psakkanan/work/guidewire/team-city/stash/in-dev-cluster/in-pd-gwcp-provisioner/scripts/update-tenant-env-ssm-secrets.py  -t newalmaden -e ps -p "%env.CONFIGURATION%"             
            """.trimIndent()
        }
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
