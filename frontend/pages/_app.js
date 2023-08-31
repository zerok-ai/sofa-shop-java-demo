import '../styles/globals.css'
import Layout from '../layouts/layout'
import { STATIC_INVENTORY } from "../utils/staticInventory"

function Ecommerce({ Component, pageProps }) {
  const categories = STATIC_INVENTORY.reduce((acc, next) => {
    next.categories.map((category) => {
      if (acc.includes(category)) return
      acc.push(category)
    })
    return acc
  }, [])
  return (
    <Layout categories={categories}>
      <Component {...pageProps} />
    </Layout>
  )
}

export default Ecommerce