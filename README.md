# integration-adapter-dsl

Concept of a reactive processing pipeline builder taking advantage of Kotlin DSL.

Uses Reactor 3 internally to link components together.

Example of Builder DSL:
```kotlin    
Router { //pipelines are created in a Router singleton
            pipeline("Count letters from console input") { //pipeline name
                components {
                    link(stdOutReader() to letterCounter()) //links two components together
                    link(letterCounter() to stdOutWriter())
                }
            }
            pipeline("Generate random numbers") {
                eventBus(randomNumbersPipelineEventBus()) //optional external event bus can be set here
                components {
                    link(randomNumberGenerator() to randomNumberSplitter())
                    link(randomNumberMapper() to stdOutWriter2())
                }
            }
        }
    }
```

All components should implement one of the following interfaces:
- `interface Source<T> : ReactiveComponent<T>` - publishes data to event bus. Usually first component in a pipeline
- `interface Sink<T> : ReactiveComponent<T>` - subscribes to event bus events. Usually last component in a pipeline
- `interface Processor<T> : Source<T>, Sink<T>` - used for intermediate result processing
