/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: 'http://127.0.0.1:7001/api/:path*'
            },
        ]
    }
}

module.exports = nextConfig
