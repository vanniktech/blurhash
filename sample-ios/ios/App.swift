import SwiftUI
import blurhash

@main
struct SampleApp : App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        VStack {
            // Sample image.
            let image = UIImage(named: "blueberries")!
            Image(uiImage: image)

            // Blur hashing.
            let blurHash = BlurHash.shared.encode(
                uiImage: image,
                componentX: 5,
                componentY: 4
            )
            Text(blurHash ?? "Invalid image")
                .padding()

            // Create blurred version.
            // We don't need to create an UIImage in its full size.
            // Let iOS scale it up for us as scaling is cheaper than generating a larger image.
            if let blurHash = blurHash, let blurred = BlurHash.shared.decode(
                blurHash: blurHash,
                width: image.size.width / 4,
                height: image.size.height / 4,
                punch: 1.0,
                useCache: true
            ) {
                Image(uiImage: blurred)
                    .scaleEffect(4)
                    .frame(width: image.size.width, height: image.size.height)
            }
        }
    }
}
