#!/usr/bin/env groovy

import static groovy.io.FileType.FILES

def scriptFile = new File(getClass().protectionDomain.codeSource.location.path)
def scriptDir = scriptFile.parent

String currentDir = new File(".").getAbsolutePath()

def libDir = new File(scriptDir + File.separator + "..");

def userLibDir = new File(currentDir + File.separator + "lib");

if (libDir.exists()) {
    libDir.eachFileRecurse(FILES) {
        if (it.name.endsWith('.jar')) {
            this.getClass().classLoader.rootLoader.addURL(it.toURI().toURL())
        }
    }
}

if (userLibDir.exists()) {
    userLibDir.eachFileRecurse(FILES) {
        if (it.name.endsWith('.jar')) {
            this.getClass().classLoader.rootLoader.addURL(it.toURI().toURL())
        }
    }
}

def cli = new CliBuilder(usage: scriptFile.name +
""" [options] scenario [-- input_files]
scenario is a sequence of blocks and scenarios (Scen::* modules or *.scen files)
options:"""
)

cli.with {
    d longOpt:'dump_scenario', 'Just dump (print to STDOUT) the given scenario and exit.'
    q longOpt:'quiet', 'Warning, info and debug messages are suppressed. Only fatal errors are reported.'
    h longOpt:'help', 'Shows help.'
}

def opts = cli.parse(args)
if(!opts || opts.help) {
    cli.usage()
    return
}

def runInstance = Class.forName("cz.ufal.udapi.core.Run").newInstance()

if (opts.arguments().isEmpty()) {
    runInstance.run(opts.dump_scenario?:false, opts.quiet?:false)
} else {
    runInstance.run(opts.dump_scenario?:false, opts.quiet?:false, opts.arguments().toArray(new String[0]))
}
