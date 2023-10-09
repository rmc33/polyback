# polyback

mvn clean package jib:build

gcloud beta run deploy {projectname} --image gcr.io/{gcp name}/{project name} --platform managed --region us-central1 --memory 512M