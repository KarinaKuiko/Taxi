services=(
"driver"
"eureka-server"
"passenger"
"rating"
"ride")
for service in "${services[@]}"; do
  echo "Build ${service}"
  docker build -f Dockerfile --build-arg SERVICE_NAME="$service" -t taxi/"$service" ../"$service"
  if [ $? -ne 0 ]; then
    echo "Failed to build $service"
    exit 1
  fi
done

echo "Services built successfully"
