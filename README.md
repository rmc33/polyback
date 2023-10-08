# polyback

mvn clean package jib:build

gcloud beta run deploy polyback --image gcr.io/strong-imagery-341902/polyback --platform managed --region us-central1 --memory 512M