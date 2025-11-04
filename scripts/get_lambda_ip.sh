docker ps --filter "name=lambda" --format "{{.ID}} {{.Names}}" | while read cid name; do
    ip=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $cid)
    echo "Lambda Container: $name"
    echo "Container ID: $cid"
    echo "IP Address: $ip"
    docker exec -it $cid ss -tnlp | grep 5005 || echo "Port 5005 not listening"
    echo "---------------------------"
done