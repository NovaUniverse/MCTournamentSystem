/**
 * Data transfer object for managed servers
 */
export default interface ServerDTO {
    is_running: boolean
    last_exception: string
    java_runtime: string
    name: string
    exit_code: number
    jvm_arguments: string
    jar: string
    last_state_report: ServerStateReport
    auto_start: boolean
}

export interface ServerStateReport {
    software?: ServerSoftware
    port?: number
    plugins?: Plugin[]
    modules?: Module[]
}

export interface ServerSoftware {
    bukkit: BukkitVersion
    java: JavaVersion
}

export interface BukkitVersion {
    bukkit_version: string
    name: string
    version: string
}

export interface JavaVersion {
    vm_version: string
    name: string
    vm_vendor: string
    version: string
    vm_name: string
}

export interface Plugin {
    name: string
    version: string
    enabled: boolean
    authors: string
}

export interface Module {
    name: string
    class_name: string
    enabled: boolean
}
