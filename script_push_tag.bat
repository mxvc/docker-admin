set  "tag_name=v2.0.4"


git tag -d %tag_name%

git push origin --delete %tag_name%

git tag %tag_name%

git push origin %tag_name%


