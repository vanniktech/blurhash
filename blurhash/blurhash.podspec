Pod::Spec.new do |spec|
    spec.name                     = 'BlurHash'
    spec.version                  = '0.2.0-SNAPSHOT'
    spec.homepage                 = 'https://github.com/vanniktech/blurhash'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'Niklas Baudy'
    spec.license                  = 'MIT'
    spec.summary                  = 'BlurHash support for iOS, Android and JVM via Kotlin Multiplatform'
    spec.vendored_frameworks      = 'build/cocoapods/framework/blurhash.framework'
    spec.libraries                = 'c++'
                
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':blurhash',
        'PRODUCT_MODULE_NAME' => 'blurhash',
    }
                
    spec.script_phases = [
        {
            :name => 'Build BlurHash',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$COCOAPODS_SKIP_KOTLIN_BUILD" ]; then
                  echo "Skipping Gradle build task invocation due to COCOAPODS_SKIP_KOTLIN_BUILD environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end