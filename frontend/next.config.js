//js
module.exports = () => {
    const rewrites = () => {
      return [
        {
          source: "/api",
          destination: "https://localhost:8080",
        },
      ];
    };
    return {
      rewrites,
      output: "standalone",
    }
  };
  