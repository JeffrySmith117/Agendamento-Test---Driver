/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: "#B91C1C",
          dark: "#7F1D1D",
        },
      },
    },
  },
  plugins: [],
};
