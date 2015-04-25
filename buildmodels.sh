#!/usr/bin/env sh
# ./buildmodels.sh <training corpus dir> <training edit1s file> <extra>(optional)

java -Xmx1024m -cp "bin:jars/*" edu.stanford.cs276.BuildModels $@

