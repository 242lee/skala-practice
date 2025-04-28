apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
  name: ${USER_NAME}-${SERVICE_NAME}
  namespace: ${NAMESPACE}
spec:
  ingressClassName: public-nginx
  rules:
  - host: springboot.skala25a.project.skala-ai.com
    http:
      paths:
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8080
        path: /api
        pathType: Prefix
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8081
        path: /actuator
        pathType: Prefix
      - backend:
          service:
            name: ${USER_NAME}-${SERVICE_NAME}
            port:
              number: 8080
        path: /swagger
        pathType: Prefix
      - backend:
          service:
            name: ${USER_NAME}-stock-ui
            port:
              number: 8080
        path: /
        pathType: Prefix
  tls:
  - hosts:
    - '*.skala25a.project.skala-ai.com'
    secretName: skala25-project-tls-cert
