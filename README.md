# kotlin-backup-retention
Small tool to delete old backups based on filenames. 

Written in Kotlin with love, because it's awesome.

**Use with caution, although the code has close to 100% test coverage, there might be bugs.
I recommend to only run it manually and with --dry to check what would happen.
You've been warned.**
  
## application.properties

Below are currently supported retention settings with their default values.
As with every spring-boot application, there are multiple ways to override those.

See [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for details.

    # regex that is used to extract the date from filenames
    retention.date-regex-pattern = .*_(\d{4}-\d{2}-\d{2})_.*
        
    # java date format that is used to parse the extracted date (has to match the regex)
    retention.date-format = yyyy-MM-dd
         
    # number of daily backups to keep
    retention.daily.keep = 7
        
    # number of weekly backups to keep
    retention.weekly.keep = 8
        
    # day of week for weekly backups
    retention.weekly.day-of-week = SUNDAY
        
    # number of monthly backups to keep
    retention.monthly.keep = 36
        
    # day of month for monthly backups
    retention.monthly.day-of-month = 1
        
    # maximum percentage of files allowed to delete without --force 
    retention.files.max-percent-delete = 10
        
    # minimum number of files to keep per directory (this counts per subdirectory)
    # this keeps files if no other rule applies (e.g a directory that only contains very old backups)
    retention.files.min-keep-per-directory = 10
        
    # list of regex patterns for files that should be processed
    # all files that match at least one of those patterns are taken into consideration
    retention.files.file-name-regex-patterns = 

**Most settings have useful defaults, but for safety reasons `retention.files.file-name-regex-patterns`
has none. You need to set this, otherwise no files will be processed.**

An `application.properties` file with
 
    retention.files.file-name-regex-patterns = .*\\.zip,.*\\.tgz,.*\\.tar\\.gz,.*\\.tar\\.bz2
    
should be enough to get you started if you are fine with the other defaults.    
    
## usage

You can either run via gradle or directly with java. After `gradle build`, the jar can be copied from build/libs.

    gradle bootRun [OPTION]... DIRECTORY...        
        
    OR    
        
    java -jar kotlin-backup-retention-1.0.jar [OPTION]... DIRECTORY...
        
    Options
        --verbose                   output more info      
        --dry                       dry run (don't delete anything)
        --force                     force deletion even if max-percent-delete is reached  
                                    (take care, use together with --dry first!)
        --fake-date=DATE            use DATE instead of current date 
                                    (take care, use together with --dry first)
        --property=VALUE            use VALUE for specified property from application.properties                            

Any property from `application.properties` can also be provided via command-line.

    retention.files.min-keep-per-directory = 20
    
becomes    

    --retention-files-minKeepPerDirectory=20

As mentioned, please read [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for details on the many possible ways to set property values.