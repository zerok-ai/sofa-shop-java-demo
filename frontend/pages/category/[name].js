import Head from 'next/head'
import ListItem from '../../components/ListItem'
import { titleIfy, slugify } from '../../utils/helpers'
import fetchCategories from '../../utils/categoryProvider'
import inventoryForCategory from '../../utils/inventoryForCategory'
import CartLink from '../../components/CartLink'
import { Fragment } from "react"
import PageHeader from "../../components/PageHeader"

const Category = (props) => {
  const { inventory, title } = props
  if (!inventory.length) {
    return (
      <Fragment>
        <PageHeader />
        <h6 className="mb-3" style={{ height: "60vh" }}>
          Could not fetch category.
        </h6>
      </Fragment>
    )
  }
  return (
    <>
      <CartLink />
      <Head>
        <title>Jamstack ECommerce - {title}</title>
        <meta name="description" content={`Jamstack ECommerce - ${title}`} />
        <meta
          property="og:title"
          content={`Jamstack ECommerce - ${title}`}
          key="title"
        />
      </Head>
      <div className="flex flex-col items-center">
        <div className="max-w-fw flex flex-col w-full">
          <div className="pt-4 sm:pt-10 pb-8">
            <h1 className="text-5xl font-light">{titleIfy(title)}</h1>
          </div>

          <div>
            <div className="flex flex-1 flex-wrap flex-row">
              {inventory.map((item, index) => {
                return (
                  <ListItem
                    key={index}
                    link={`/product/${slugify(item.name)}`}
                    title={item.name}
                    price={item.price}
                    imageSrc={item.image}
                  />
                )
              })}
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export async function getServerSideProps({ params }) {
  try {
    const category = params.name.replace(/-/g, " ")
    const inventory = await inventoryForCategory(category)
    return {
      props: {
        inventory,
        title: category,
      },
    }
  } catch {
    const category = params.name.replace(/-/g, " ")
    return {
      props: {
        inventory: [],
        title: category,
      },
    }
  }
}

export default Category