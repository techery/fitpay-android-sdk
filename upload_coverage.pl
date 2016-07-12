#!/usr/bin/perl
# This script uploads a (weekly) coverage report to github pages using a token.
# the token should be specified in the environment variables when running this
# original credit/howto available at: http://sleepycoders.blogspot.com/2013/03/sharing-travis-ci-generated-files.html

# only push coverage results for non-pull requests (which would normally put us on a branch build)
# that are on the develop/master branch AND that were successful
#
# You need a personal oauth token (settings, personal access tokens) - make sure that full repo privs (including for private repos is set)
#
use strict;
use warnings;

if (! (
       ($ENV{TRAVIS_EVENT_TYPE} eq "cron") &&
       ($ENV{TRAVIS_PULL_REQUEST} eq "false") &&
       ($ENV{TRAVIS_TEST_RESULT} == 0) && #build succeeded on a 0
       (($ENV{TRAVIS_BRANCH} eq "develop") || ($ENV{TRAVIS_BRANCH} eq "master") || ($ENV{TRAVIS_BRANCH} =~ /coverage/))
      )
   ) {
    print "I only run coverage uploads for 'develop' or 'master' branch builds\n";
    exit 0;
}

my $now = `date -Iminutes`;

my $cvgdir = `pwd`; chop $cvgdir;

#assuming we need to configure git every time.
chdir("$ENV{HOME}") || die ("Couldn't chdir to HOME");
system("git config --global user.email 'peers\@mtnboy.net'");
system("git config --global user.name 'Eric Peers'");
if (! -d "pageycheckout") {
    print "Checking out github pages\n";
    system("git clone --quiet https://$ENV{GH_TOKEN}\@github.com/fitpay/fitpay.github.io.git pageycheckout> /dev/null");
} else {
    print "github pages already present\n";
}
chdir("pageycheckout/android-sdk") || die ("Couldn't chdir to pageycheckout/android-sdk");

print "cp -R $cvgdir/fitpay/build/reports/jacoco/testDebugUnitTestCoverage/html ./coverage_$now\n";
system("cp -R $cvgdir/fitpay/build/reports/jacoco/testDebugUnitTestCoverage/html ./coverage_$now");
system("git add -f ./coverage_$now");
open(OUTFILE, ">index.html.new") || die ("Couldn't open index.html.new");
open(INFILE, "<index.html") || die ("Couldn't open index.html for read-writing");

while (<INFILE>) {
    print OUTFILE $_;

    if (/STARTCVG/) {
        print OUTFILE "<a href=coverage_$now>Results from $now</a><br>\n";
    }
}
close (OUTFILE);
close (INFILE);
system("mv index.html.new index.html");
system("git add index.html");

system("git commit -m 'pushing coverage data'");
system("git push");

exit 0;
