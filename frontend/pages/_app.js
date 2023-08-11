import '../styles/globals.css'
import Layout from '../layouts/layout'
import fetchCategories from '../utils/categoryProvider'

function Ecommerce({ Component, pageProps, categories }) {
  return (
    <Layout categories={categories}>
      <Component {...pageProps} />
    </Layout>
  )
}

Ecommerce.getInitialProps = async () => {
  try {
    const categories = await fetchCategories()
    return {
      categories,
    }
  } catch (err) {
    return {
      categories: [],
    }
  }
}

export default Ecommerce