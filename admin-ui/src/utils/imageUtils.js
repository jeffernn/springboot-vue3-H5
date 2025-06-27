export function getImageUrl(relativePath) {
  if (!relativePath) return '';
  const host = import.meta.env.VITE_APP_API_URL || 'http://localhost:8080';
  return relativePath.startsWith('/upload')
    ? `${host}${relativePath}`
    : `${host}/upload/${relativePath}`;
}
