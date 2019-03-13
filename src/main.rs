#![windows_subsystem = "windows"]

use std::sync::{Arc, Mutex};

use web_view::*;

use serde_derive::*;
use serde_json;

use lazy_static::*;

use tera::compile_templates;
use tera::{Context, Tera};

fn main() {
    let count = Arc::new(Mutex::new(0));
    let mut context = Context::new();
    context.insert("title", "Cljs Everywhere!");
    context.insert("style", &inline_style(include_str!("view/css/clock.css")));
    context.insert("ui", &inline_script(include_str!("view/js/ui.js")));
    let mut tera = Tera::default();
    tera.autoescape_on(vec!["html", ".sql"]);
    tera.add_raw_template("html_templ", include_str!("view/index.html"));
    let html = tera.render("html_templ", &context).unwrap_or(String::from("BROKEN"));
    //    dbg!(&html);
    web_view::builder()
        .title("Rust / Elm - Counter App")
        .content(Content::Html(html))
        .size(320, 480)
        .resizable(false)
        .debug(true)
        .user_data(0 as i32)
        //        .user_data(0 as i32)
        .invoke_handler(|webview, arg| {
            use Operation::*;
            dbg!(&webview);
            dbg!(&arg);
            let op = serde_json::from_str(&arg).unwrap_or(Init { n: 0 });
            match op {
                Init { n } => init(Arc::clone(&count), n),
                Increment { n } => increment(Arc::clone(&count), n),
                Decrement { n } => decrement(Arc::clone(&count), n),
            };
            update(webview, Arc::clone(&count))
        })
        .run()
        .unwrap();
}

fn update(webview: &mut WebView<i32>, count: Arc<Mutex<i32>>) -> WVResult {
    let count = count.lock().expect("Could not lock the Count mutex");
    dbg!(&*count);
    //    let currentCount = webview.user_data_mut();
    //    *currentCount = *count as i32;
    //    webview.set_title(&format!("Current Count: ({})", currentCount)).unwrap();
    // webview.set_title(&format!("Current Count: ({})", &*count));
    //    let updateCountJs = format!("rpc.render({})", serde_json::to_string(currentCount).unwrap());
    //    let update_count_js = format!("rpc.render({});", serde_json::to_string(&format!("{}", *count)).unwrap_or(String::from("N/A")));
    // let mut context = Context::new();
    // context.insert("update_count", &*count);
    // let update_count_js = TEMPLATES.render("rpc_render.js", &context).unwrap();
    // dbg!(&update_count_js);
    // //    webview.eval(&updateCountJs)
    // webview.eval(&update_count_js)
    dbg!(&*count);
    webview.eval("console.log('Callback from Rust :-)');")
}

#[derive(Debug, Serialize, Deserialize)]
#[serde(tag = "op", rename_all = "camelCase")]
pub enum Operation {
    Init { n: i32 },
    Increment { n: i32 },
    Decrement { n: i32 },
}

fn init(count: Arc<Mutex<i32>>, n: i32) {
    let mut count = count.lock().expect("Could not lock the Count mutex");
    *count = n;
}

fn increment(count: Arc<Mutex<i32>>, n: i32) {
    let mut count = count.lock().expect("Could not lock the Count mutex");
    *count += n;
}

fn decrement(count: Arc<Mutex<i32>>, n: i32) {
    let mut count = count.lock().expect("Could not lock the Count mutex");
    *count -= n;
}

fn inline_script(s: &str) -> String {
    format!(r#"<script type="text/javascript">{}</script>"#, s)
}

fn inline_style(s: &str) -> String {
    format!(r#"<style>{}</style>"#, s)
}
