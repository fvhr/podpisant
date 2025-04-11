FROM openresty/openresty:jammy

RUN luarocks install lua-resty-jwt

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]